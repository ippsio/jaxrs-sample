$(function() {
		new Vue({
			el : '#app',
			data : {
				message : 'Hello Vue.js!'
			}
		})

		var myData = {
			"items":[
				{ "itemCd":91, "itemNm":"塩ラーメン", "itemPrice":300},
				{ "itemCd":94, "itemNm":"味噌ラーメン", "itemPrice":290},
				{ "itemCd":95, "itemNm":"豚骨ラーメン", "itemPrice":3500}
			]
		};
		for (var i=0; i<myData.items.length; i++){
			console.log(myData.items[i].itemCd + "　" + myData.items[i].itemNm + "　" + myData.items[i].itemPrice);
		}
});
