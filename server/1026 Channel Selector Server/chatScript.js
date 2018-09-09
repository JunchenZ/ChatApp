var body = document.getElementsByTagName("body")[0]; 
body.style.textAlign = "center";
var h1 = document.createElement("h");
h1.innerText = "Web Chat";
var p_username = document.createElement("p");
p_username.innerText = "Username";
var input_username = document.createElement("input");
var p_room = document.createElement("p");
p_room.innerText = "Room";
var input_room = document.createElement("input");
var button_login = document.createElement("button");
button_login.innerText = "login";

body.appendChild(h1);
body.appendChild(p_username);
p_username.appendChild(input_username);
body.appendChild(p_room);
p_room.appendChild(input_room);
body.appendChild(document.createElement("p"));
body.appendChild(button_login);

button_login.onclick = function(){
    roomOpen();
}

function roomOpen(){

    var url = "ws://" + location.host;
    var mySocket = new WebSocket(url);
    var error = false;
    var username = input_username.value;
    var room = input_room.value;
    body.innerHTML = "";

    // if(username.includes("\"")){
    //     body.innerText = "error, username can not include quotation marks\n";
    //     error = true;
    // }
    if(!error){
        var p_chat = document.createElement("p");
        var input_chat = document.createElement("input");
        var button_send = document.createElement("button");
        button_send.innerText = "send";
    
        mySocket.onopen = function(event){
    
            mySocket.send("join " + room);     
            var json_msg;
            body.innerText = "Room " + room;
    
            body.appendChild(document.createElement("p"));
            body.appendChild(input_chat);   
            body.appendChild(button_send);
            body.appendChild(p_chat);        
            
            button_send.onclick = function(event){
                var message = input_chat.value;
                mySocket.send(username + " " + message); 
            };  
            
            mySocket.onmessage = function(event){
                json_msg = JSON.parse(event.data);
                p_chat.innerText += "\n" + json_msg.user + ":   " + json_msg.message + "\n";
            };   
    
        };
    }
    

    
}



