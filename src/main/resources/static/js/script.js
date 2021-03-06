console.log("javascript is connected")


const toggleSidebar = () => {
	
	if($(".sidebar").is(":visible")){
		// true
		$(".sidebar").css("display","none")
		$(".content").css("margin-left","0%")
	}
	else{
		// false
		$(".sidebar").css("display","block")
		$(".content").css("margin-left","20%")
	}
}

const search = () =>{
	console.log("searching////")
	
	let query = $("#search-input").val()
	
		if(query==""){
		
			$(".search-result").hide()
	}else{
		
		// Sending request to server
		let url = `http://localhost:8282/search/${query}`;
		
		fetch(url).then((response) => {
			return response.json();
		}).then(data => {
			console.log(data)
			
			let text = `<div class="list-group">`
				
				data.forEach((contact) => {
					text+=`<a href="/user/${contact.cId}/contact" class="list-group-item list-group-item-action">${contact.name}</a>`
				})
				
				text+=`</div>`
					
					$(".search-result").html(text);
					$(".search-result").show();
					
		})
		
		console.log(query)
		
	}
}


// first request - to server to create order
const paymentStart = () =>{
	
	console.log("payment started")

	let amount = $("#payment_field").val();
	console.log(amount)
	
	if(amount=='' || amount==null){
		//alert("amount is required...")
		swal("Failed !!", "Amount is required...", "error");
		return;
	}
	
	// ajax to send request to server to create order
	$.ajax(
			{
				url:'/user/create_order',
				data:JSON.stringify({amount:amount,info:'order_request'}),
				contentType:'application/json',
				type:'POST',
				dataType:'json',
				success:function(response){
					// invoked where success
					console.log(response)
					if(response.status=="created"){
						// open payment form
						let options={
								key: 'rzp_test_z73Yi5BEZxQOrd',
								amount: response.amount,
								currency: 'INR',
								name: 'Contact Manager',
								description: 'payment',
								image: 'https://example.com/your_logo',
								order_id: response.id,
								handler: function (response){
									console.log(response.razorpay_payment_id)
									console.log(response.razorpay_order_id)
									console.log(response.razorpay_signature)
									console.log("payment successful")
									
									updatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,'paid');
									swal("Good job!", "Congrates !! payment successful", "success");
									          							
								},
								prefill: {
									name: "",
									email: "",
									contact: ""
									},
									notes: {
										address: "Contact Payment"

										},
										theme: {
										color: "#3399cc"
										},				
						};
						let rzp = new Razorpay(options);
						
						rzp.on('payment.failed', function (response){
							console.log(response.error.code);
							console.log(response.error.description);
							console.log(response.error.source);
							console.log(response.error.step);
							console.log(response.error.reason);
							console.log(response.error.metadata.order_id);
							console.log(response.error.metadata.payment_id);
							//alert("Oops Payment Failed !!!")
							swal("Failed !!", "Oops payment failed !!!!", "error");
							});
						
						
						rzp.open();
					}
				},
				error:function(error){
					// invoked where error
					console.log(error)
					alert("something whent wrong...")
				}
			}
			)
}


//function define

function updatePaymentOnServer(payment_id,order_id,status)
{
	
	$.ajax({
		url:'/user/update_order',
		data:JSON.stringify({payment_id:payment_id,order_id:order_id,status:status}),
		contentType:'application/json',
		type:'POST',
		dataType:'json',
		success:function(response){
			swal("Good job!", "Congrates !! payment successful", "success");
		},
		error:function(error){
			swal("Failed !!", "Your payment is successful, but we did not get in serever", "error");
		}
	})
			
}

