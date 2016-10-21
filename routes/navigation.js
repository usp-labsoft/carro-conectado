var express = require('express');
var router = express.Router();

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
    var date = req.body.date;
    var user_id = req.body.user_id;
    var arduino_id = req.body.arduino_id;

    // Set our collection
    var collection = db.get('navigation');

    // Submit to the DB
    collection.insert({
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
            // And forward to success page
            res.redirect("navigationlist");
        }
    });
});

module.exports = router;
