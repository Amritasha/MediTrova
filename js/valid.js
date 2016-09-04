var xmlhttp = new getXMLObject();	//xmlhttp holds the ajax object

function getXMLObject(){ //XML OBJECT
    var xmlHttp = false;
    try{
	       xmlHttp = new ActiveXObject("Msxml2.XMLHTTP") // For Old Microsoft Browsers
    }
	catch(e1){
		try{
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP") // For Microsoft IE 6.0+
		}
		catch (e2){
			xmlHttp = false // No Browser accepts the XMLHTTP Object then false
		}
	}
	if(!xmlHttp && typeof XMLHttpRequest != 'undefined'){
	          xmlHttp = new XMLHttpRequest(); //For Mozilla, Opera Browsers
    }
    return xmlHttp; // Mandatory Statement returning the ajax object created
}


function handleServerResponse1(){
	if(xmlhttp.readyState == 4){
		if(xmlhttp.status == 200){
            document.getElementById("disp").innerHTML=xmlhttp.responseText.trim();
        }
    }
}


function ajaxFunction(){
    if(document.getElementById('custom').value.length>2){
		if(xmlhttp){
            xmlhttp.open("GET","@"+ document.getElementById('custom').value.trim(),true)//for asynchronus); //gettime will be the servlet name
			xmlhttp.onreadystatechange = handleServerResponse1;
			xmlhttp.send(null);
		}
	}
	else{
		document.getElementById("disp").innerHTML="";
	}
}


function handleServerResponse2(){
	if(xmlhttp.readyState == 4){
		if(xmlhttp.status == 200){
            var store = xmlhttp.responseText.trim();
            if(store.substring(0, 3) == 'ok='){

                display(store);
            }
            else{
            //    res.style.display = "block";
                document.getElementById("disp").innerHTML=store;
            }
        }
        else{
            document.getElementById("disp").innerHTML="An error occurred shortening that link";
        }
    }
}


function done(){
    // event.preventDefault(); prevent unwanted redirection by onsubmit event
     if(xmlhttp){
     var url = document.getElementById('url').value.trim();
     var custom = document.getElementById('custom').value.trim();

     xmlhttp.open("POST","create/",true); //getname will be the servlet name
     // / after create cos /create/ will be the result and this is how it is mapped
     xmlhttp.onreadystatechange  = handleServerResponse2;
     xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
     xmlhttp.send("url="+url+"&"+"custom="+custom); //Posting url and custom to Servlet
    }
}


function go() {


    var url = document.getElementById('url').value.trim();
    var custom = document.getElementById('custom').value.trim();
    document.getElementById('url').value= url;
    document.getElementById('custom').value= custom;


    if (url == '' || url == 'http://' ){
            document.getElementById("disp").innerHTML= "URL is not entered";
            return false;
    }

    if(!validURL(url)){
        return false;
    }

    if( !(custom == '') ){
        if(!validcustom(custom)){
            return false;
        }
        if(document.getElementById("disp").innerHTML != "Available"){
                document.getElementById("disp").innerHTML= "Custom URL is Already Taken";
                return false;
        }
    }
    done();

    return true;
}


function validURL(str) {
    if(str.indexOf("http://")==0 || str.indexOf("https://")==0 || str.indexOf("www.")==0){
        return true;
    }
    else{
        document.getElementById("disp").innerHTML="Please enter Valid URL";
        return false;
    }
}


function validcustom(str1){

    if(str1.length <= 2 ){
        document.getElementById("disp").innerHTML="Custom URL must have atleast 3 letters ";
        return false;
    }

    var regexp = new RegExp("^[a-zA-Z0-9-_]+$");
    var x = regexp.test(str1);

    if(!x){
        document.getElementById("disp").innerHTML= "Custom URL may contain letters, numbers, and dashes only.";

    }

    return x;
}


function copy() {

        var copyTextareaBtn = document.querySelector('.js-textareacopybtn');
        var copyTextarea = document.querySelector('.js-copytextarea');
        copyTextarea.select();
        try {
            var successful = document.execCommand('copy');
            if(successful){
                alert("Link copied to Clipboard");
            }
            else{
                alert("Copying Unsuccessful");
            }
        } catch (err) {
            alert('Oops, unable to copy');
        }
}


function display(str){
    // final is hide befroe change
    hidef();


    document.getElementById("final").visibility="none";

    document.getElementById("url").value = "";
    document.getElementById("custom").value = "";
    document.getElementById("disp").innerHTML = "";

   // alert("http://miny.ml"+str.substring(3));
    document.getElementById("fin").value="http://ocul.in"+str.substring(3);
    document.getElementById("hi").value="http://ocul.in"+str.substring(3);
    showf();
}


// for fading effect;
var showf,hidef;

$(document).ready(function(){

    hidef = function(){
        $("#final").hide();
    }

    showf = function(){
        $("#final").slideDown("slow");
    }
})
