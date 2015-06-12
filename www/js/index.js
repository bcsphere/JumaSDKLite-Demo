/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.JumaSDKLite = JumaSDKLite;
        app.JumaSDKLite.onDeviceDiscovered(app.onDeviceDiscovered);
        app.JumaSDKLite.onMessageReceivered(app.onMessageReceivered);
        app.JumaSDKLite.onError(function(message){
            console.log(JSON.stringify(message));
        });
        app.bindUIEvent();
    },

    bindUIEvent : function(){
        $('#scanbtn').click(function(){
            var text = $('.ui-btn-text',this).html();
             if(text == 'stopScan'){
              app.JumaSDKLite.stopScan();
              $('.ui-btn-text',this).html('Scan');
            }else if(text == 'Scan'){
              app.JumaSDKLite.startScan();
              $('.ui-btn-text',this).html('stopScan');
            }
        });

        $('#disconnect').click(function(){
            app.JumaSDKLite.disconnect(function(){
                app.device = null;
                $('#deviceList').html('');
                $('#message').val('');
                $('#messageContent').html('');
                $.mobile.changePage("#mainPage", "slideup");
            },function(error){console.log(JSON.stringify(error))},app.device.uuid);
        });

        $('#send').click(function(){
            app.JumaSDKLite.sendMessage(function(){
                if($('#messageContent').children().length>=1){
                    $('<p>'+new Date().toLocaleTimeString()+" send :"+$('#message').val()+'</p>').insertBefore($('#messageContent').children()[0]);
                }else{
                    $('#messageContent').append('<p>'+new Date().toLocaleTimeString()+" send :"+$('#message').val()+'</p>');
                }
            },function(arg){
                console.log(JSON.stringify(arg));
            },$('#message').val());

        });
    },

    onDeviceDiscovered : function(arg){
        var deviceList = $('#deviceList');

        var li = $('<li><a href="#"><h4> Name : '+arg.name+'</h4><p>RSSI : '+arg.rssi+'</p><p> UUID : '+arg.uuid+'</p></a></li>');

        li.click(function(){
            app.JumaSDKLite.connect(function(){
                app.JumaSDKLite.stopScan();
                $('.ui-btn-text',$('#scanbtn')).html('Scan');
                app.device = {'name':arg.name,'uuid':arg.uuid,'rssi':arg.rssi};
                $('#message').val('');
                $('#messageContent').html('');
                $.mobile.changePage("#optionPage", "slideup");
            },function(){

            },arg.uuid);
        });

        deviceList.append(li);

        deviceList.listview("refresh")
    },

    onMessageReceivered : function(arg){
        if($('#messageContent').children().length>=1){
            $('<p>'+arg.currentDate+"  received "+arg.message+'</p>').insertBefore($('#messageContent').children()[0]);
        }else{
            $('#messageContent').append('<p>'+arg.currentDate+"  received "+arg.message+'</p>');
        }
    },
    
};
