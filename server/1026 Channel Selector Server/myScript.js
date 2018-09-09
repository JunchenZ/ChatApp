   

var url = "ws://" + location.host;
var mySocket = new WebSocket(url);
    
mySocket.onopen = function(event){

    console.log("handshake is working");

    var message1 = "join room";
    var message2 = "test for receiving message";

    mySocket.send(message1);
    mySocket.send(message2);
    

    mySocket.onmessage = function(event){
        console.log(event.data);
    }
     
};



