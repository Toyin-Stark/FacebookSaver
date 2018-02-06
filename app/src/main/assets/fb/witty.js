'use strict';

function test(video,photo) {

if($('article').length != 0){
       $('article').each(function(){

         if($(this).find(".story_body_container").length != 0){
                  var button = document.createElement("button");
                  button.setAttribute("type","button");
                  button.setAttribute("id","save");
                  button.setAttribute("class","downloadBtn");

               if($(this).find(".downloadBtn").length != 0){


               }else{


                   if($(this).find("._53mw._4gbu").length != 0){
                   var tut = $(this).find("._53mw._4gbu").attr("data-store");
                    button.setAttribute("data",tut);
                    button.innerHTML = video;

                    $(this).find('._22rc').prepend(button);


                   }


                   if($(this).find("._147s").length != 0){
                   var tut1 = $(this).find("._147s").find("i").attr("style");
                    button.setAttribute("data",tut1);
                    button.innerHTML = photo;

                    $(this).find('._22rc').prepend(button);


                   }




               }





         }

       });
}


if($('._3f50').length != 0){
    $('._3f50').each(function(){

                 var button = document.createElement("button");
                  button.setAttribute("type","button");
                  button.setAttribute("id","save");
                  button.setAttribute("class","downloadBtn");

                   if($(this).find(".downloadBtn").length != 0){


                   }else{


                      if($(this).find("._53mw._4gbu").length != 0){
                          var videoIDS = $(this).find("._53mw._4gbu").attr("data-store");
                          button.setAttribute("data",videoIDS);
                          button.innerHTML = video;

                          $(this).find('._22rc').prepend(button);

                      }


                       if($(this).find("._147s").length != 0){
                          var tut1 = $(this).find("._147s").find("i").attr("style");
                           button.setAttribute("data",tut1);
                           button.innerHTML = photo;
                           $(this).find('._22rc').prepend(button);


                       }



                   }










    });


}


 $('.downloadBtn').on("touchend", function (e) {
     var blonde = $(this).attr('data')
     e.preventDefault();
      window.JSInterface.startVideo(blonde);
});




}

