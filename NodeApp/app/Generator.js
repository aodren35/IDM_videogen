const express = require('express');
const common = require('../common/common');

var querystring = require('querystring');
var http = require('http');
var fs = require('fs');

const exec = require('child_process').exec;

module.exports = function (app, loggerFile) {

    // add routes to the app
    const router = express.Router();
    app.use(common.BASE_LOCAL_URL + common.API_VERSION + '/generator', router);

    // the route to post credentials
    router.post('/', function (req, res) {
        if (!req.files)
            return res.status(400).send('No files were uploaded.');

        // The name of the input field (i.e. "sampleFile") is used to retrieve the uploaded file
        var sampleFile = req.files.sampleFile;

        // Use the mv() method to place the file somewhere on your server
        sampleFile.mv('vg.videogen', function(err) {
            if (err)
                return res.status(500).send(err);

            var child = exec('java -jar ./VideoGenerator.jar vg.videogen generate',
                function (error, stdout, stderr){
                    console.log('Output -> ' + stdout);
                    if(error !== null){
                        console.log("Error -> "+error);
                    }
                });
            module.exports = child;

            res.send({msg: "File upload", code: 4});
        });
    });

}
;