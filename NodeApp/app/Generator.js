const express = require('express');
const common = require('../common/common');

var querystring = require('querystring');
var http = require('http');
var fs = require('fs');

const exec = require('child_process').exec;

module.exports = function (app, loggerFile) {

    var vignettes = [
        {"url" : 'example10_4.jpg', "name" : "ba_om"},
        {"url" : 'example10_5.jpg', "name" : "yg_om"},
        {"url" : 'example10_6.jpg', "name" : "yg_asnl"},
/*        {"url" : 'example10_0.jpg', "name" : ""},
        {"url" : 'example10_0.jpg', "name" : ""},
        {"url" : 'example10_0.jpg', "name" : ""},
        {"url" : 'example10_0.jpg', "name" : ""},*/
    ];

    // add routes to the app
    const router = express.Router();
    app.use(common.BASE_LOCAL_URL + common.API_VERSION + '/generator', router);

    // the route to post credentials
    router.post('/', function (req, res) {
        var videos = req.body.videos;
        var generatedVideogen = generatedVideoGen(videos);
        fs.writeFile("vg.videogen", generatedVideogen, function(err) {
            if(err) {
                return console.log(err);
            }
            var child = exec('java -jar ./VideoGenerator.jar vg.videogen generate',
                function (error, stdout, stderr){
                    console.log('Output -> ' + stdout);
                    if(error !== null){
                        console.log("Error -> "+error);
                    }
                });
            module.exports = child;

            res.send({msg: "Succes", url: 'vg_generated'});
        });
    });

    router.get('/vignettes', function (req, res) {
        res.send(vignettes);
    });

    router.get('/variante', function (req, res){
        function os_func() {
            this.execCommand = function(cmd, callback) {
                exec(cmd, function (error, stdout, stderr){
                    if (error) {
                        console.log("Error -> "+error);
                        return;
                    }
                    callback(stdout);
            });
            }
        }
        var os = new os_func();
        os.execCommand('java -jar ./VideoGenerator.jar total.videogen generate', function (returnvalue) {
            res.send({msg: "Succes", url: 'total_generated'});
        });
        /*var child = exec('java -jar ./VideoGenerator.jar total.videogen generate',
            function (error, stdout, stderr){
                console.log('Output -> ' + stdout);
                if(error !== null){
                    console.log("Error -> "+error);
                }
            });
        module.exports = child;


        res.send({msg: "Succes", url: 'total_generated'});*/


    });

}
;