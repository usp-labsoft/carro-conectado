var express = require('express');
var router = express.Router();

/* GET Arduino List page. */
router.get('/arduinolist', function(req, res) {
    var db = req.db;
    var collection = db.get('arduino');
    collection.find({},{},function(e,docs){
        // var collectionUser = db.get('user');
        // for(var i = 0; i < docs.length; i++){
        //     collectionUser.find({"_id": docs[i].owner}, function(err, user){
        //         var owner_username = user[0].username;
        //         if(err){
        //             res.send("There was a problem getting the information from the database.");
        //         }
        //         else {
        //             docs[i-1].owner = owner_username;
        //         }
        //     });
        // }
        res.render('arduinolist', {
            "arduinolist" : docs
        });
    });
});

/* GET New Arduino page. */
router.get('/newarduino', function(req, res) {
    res.render('newarduino', { title: 'Add New Arduino' });
});

router.get('/deleteArduino', function(req, res) {
    var db = req.db;
    var id = req.query.id;
    var collection = db.get('arduino');
    collection.remove({"_id" : id},{},function(err,docs){
        if(err){
            res.send("There was a problem deleting the information from the database.");
        }
        else{
            res.redirect("arduinolist");
        }
    });
});

/* POST to Add Arduino Service */
router.post('/addarduino', function(req, res) {

    // Set our internal DB variable
    var db = req.db;

    // Get our form values. These rely on the "name" attributes
    var owner = req.body.owner;

    var collectionUser = db.get('user');

    collectionUser.find({"username": owner}, function(err, user){
        if(err){
            res.send("There was a problem adding the information to the database.");
        }
        else {
            var owner_id = user[0]._id;
            // Set our collection
            var collection = db.get('arduino');

            // Submit to the DB
            collection.insert({
                "owner" : owner_id
            }, function (err, doc) {
                if (err) {
                    // If it failed, return error
                    res.send("There was a problem adding the information to the database.");
                }
                else {
                    // And forward to success page
                    res.redirect("arduinolist");
                }
            });
        }
    });
});

module.exports = router;
