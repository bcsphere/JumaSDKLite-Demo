cordova.define("com.juma.bluetooth.sdklite.jumasdklite", function(require, exports, module) { /*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

var exec = require('cordova/exec');
var platform = require('cordova/platform');

/**
 * Provides access to notifications on the device.
 */
(function(module){
    var ACTION_START_SCAN = "com.juma.bluetooth.sdklite.ACTION_START_SCAN";
    var ACTION_STOP_SCAN = "com.juma.bluetooth.sdklite.ACTION_STOP_SCAN";
    var ACTION_DEVICE_DISCOVERED = "com.juma.bluetooth.sdklite.ACTION_DEVICE_DISCOVERED";
    var ACTION_CONNECT = "com.juma.bluetooth.sdklite.ACTION_CONNECT";
    var ACTION_CONNECTED = "com.juma.bluetooth.sdklite.ACTION_CONNECTED";
    var ACTION_DISCONNECT = "com.juma.bluetooth.sdklite.ACTION_DISCONNECT";
    var ACTION_DISCONNECTED = "com.juma.bluetooth.sdklite.ACTION_DISCONNECTED";
    var ACTION_SEND_MESSAGE = "com.juma.bluetooth.sdklite.ACTION_SEND_MESSAGE";
    var ACTION_RECEIVER_MESSAGE = "com.juma.bluetooth.sdklite.ACTION_RECEIVER_MESSAGE";
    var ACTION_ERROR = "com.juma.bluetooth.sdklite.ACTION_ERROR";
    var NAME_STR = "name";
    var UUID_STR = "uuid";
    var MESSAGE_STR = "message";
    module.exports = {
        startScan : function(successFunc,errorFunc,name) {
            if(!name){
                name = '';
            }
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_START_SCAN, [{"name":name}]);
        },
        
        onDeviceDiscovered : function(successFunc,errorFunc){
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_DEVICE_DISCOVERED, []);
        },

        stopScan : function(successFunc,errorFunc){
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_STOP_SCAN, []);
        },

        connect : function(successFunc,errorFunc,uuid){
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_CONNECT, [{"uuid":uuid}]);
        },

        disconnect : function(successFunc,errorFunc,uuid){
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_DISCONNECT, [{"uuid":uuid}]);
        },

        sendMessage : function(successFunc,errorFunc,message){
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_SEND_MESSAGE, [{"message":message}]);
        },

        onMessageReceivered : function(successFunc,errorFunc){
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_RECEIVER_MESSAGE, []);
        },

        onError : function(successFunc,errorFunc){
            cordova.exec(successFunc,errorFunc, "JumaSDKLite", ACTION_ERROR, []);
        }
    };
})(module);


});
