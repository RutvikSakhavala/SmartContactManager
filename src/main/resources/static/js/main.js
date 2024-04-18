
var search=()=>{
	
	var query = document.getElementById("search").value
	
	if(query==""){
	  $(".search-result").hide()
	}
	else{
		 
    fetch("http://localhost:8080/user/search/"+query).then(res=>res.json())
    .then(data=>{
				
		console.log(data)
		if(data){
		 $(".search-result").show()
			
			let text = `<div class='list-group'>`;
		
		data.forEach((contact)=>{
			
				text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-action'>${contact.firstName +' ' +contact.secondName}</a>`; 
		})
		
		text+= `</div>`;	
		$(".search-result").html(text);
		}
		else{
			
			$("#no").text("hello");
		}			
		});
	}
	
	
}