var express = require('express');
var router = express.Router();

function formatAsDate(inputDate){
    var date;
    if(typeof inputDate === "string"){
        date = new Date();
        var dateAsString = inputDate;
        dateAsString = dateAsString.split(" ");
        var timeAsString = dateAsString[1].split(":");
        dateAsString = dateAsString[0].split("/");
        var day = parseInt(dateAsString[0]);
        var month = parseInt(dateAsString[1]) - 1;
        var year = parseInt(dateAsString[2]);
        var hour = parseInt(timeAsString[0]); 
        var minutes = parseInt(timeAsString[1]); 
        date.setFullYear(year, month, day);
        date.setHours(hour);
        date.setMilliseconds(0);
        date.setMinutes(minutes);
        date.setSeconds(0);
    }
    else{
        date = inputDate;
    }
    return date;
}

router.get('/navigationlist', function(req, res) {
    var db = req.db;
    var collection = db.get('navigation');
    collection.find({},{},function(e,docs){
        res.render('navigationlist', {
            "navigationlist" : docs
        });
    });
});

router.get('/newnavigation', function(req, res) {
    res.render('newnavigation', { title: 'Add New Navigation' });
});

router.post('/addnavigation', function(req, res) {
    var db = req.db;

    var speed_obd = req.body.speed_obd;
    var speed_limit = req.body.speed_limit;
    var GPS = req.body.GPS;
    var date = formatAsDate(req.body.date);
    var user_id = req.body.user_id;
    var arduino_id = req.body.arduino_id;

    // Set our collection
    var navigation_collection = db.get('navigation');
    var add = false;

    navigation_collection.find({user_id: user_id}, { sort : { date : -1 } }, function(err, nav_docs){
        if(err) throw err;
        if(nav_docs){
            var last_value = nav_docs[0];
            last_value.date = formatAsDate(last_value.date);
            var timeDiff = Math.abs(last_value.date.getTime() - date.getTime()) / 60000; // time difference in minutes
            var speedDiff = Math.abs(last_value.speed_obd - speed_obd);
            // check for time difference greater than 1 minute
            if(timeDiff > 1 || speedDiff > 3){
                add = true;
            }
        }

        if(add){
            // Submit to the DB 
            navigation_collection.insert({
                "speed_obd" : speed_obd,
                "speed_limit" : speed_limit,
                "GPS" : GPS,
                "date" : date,
                "user_id" : user_id,
                "arduino_id" : arduino_id
            }, function (err, doc) {
                if (err) {
                    // If it failed, return error
                    res.send("There was a problem adding the information to the database.");
                }
                else {
                    var user_collection = db.get('user');
                    if(speed_obd > speed_limit){
                        user_collection.findAndModify({_id: user_id}, {$inc: {over_speed_count: 1}});
                    }
                    user_collection.findAndModify({_id: user_id}, {$inc: {navigation_data_count: 1}});
                    res.redirect("navigationlist");
                }
            });
        }
        else{
            res.redirect("navigationlist");
        }
    });
});

module.exports = router;
