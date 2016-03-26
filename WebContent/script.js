$(function() {
	var broakers = new Array();
	var spread_list = $('#spread_list');
	var symbol = "USDJPY";
	var ws = openWs(symbol);

	createNewSpreadListTable();

	function createNewSpreadListTable() {
		spread_list.columns({
			data : [],
			templateFile: 'custom_template.html',
			sortby : 'spread',
			schema : [
				{"header" : "ブローカー","key" : "broaker"},
				{"header" : "BID","key" : "bid"},
				{"header" : "ASK","key" : "ask"},
				{"header" : "スプレッド","key" : "spread", "template" : "{{spread}}pips"}
			],
			showRows : [ 10, 20, 50 ],
			size : 20,
			reverse: false,
			paginating : false
		});
	}

	setInterval(function() {
		if (ws.readyState === ws.CLOSED) {
			ws = openWs();
		}
		// console.log(ws.readyState);
	}, 3000);

	function openWs() {
		var url = 'ws://' + location.host + '/ws/' + symbol;
		var ws = new WebSocket(url);
		// 受信
		ws.onmessage = function(received) {
			// 検索ボックスにフォーカスがあたってる?
			var focused = $(':focus');
			var searchBoxFocused = (focused.attr('class') === 'ui-table-search');

			var json = $.parseJSON(received.data);
			if (json.items == null) {
				if (spread_list.children("div").length > 0) {
					spread_list.columns('destroy');
				}
				createNewSpreadListTable();
			} else {
				var o = spread_list.columns("getObject");
				var _sortBy = o.sortBy;
				var _query = o.query;
				var _reverse = o.reverse;
				var _template = o.template;
				var _scheme = o.scheme;
				var _reverse = o.reverse;
				var _paginating = o.paginating;
				var _size = o.size;
				if (_sortBy == null) {
					_sortBy = 'spread';
				}

				if (spread_list.children("div").length > 0) {
					spread_list.columns('destroy');
				}
				spread_list.columns({
					data : json.items,
					schema : [
						{"header" : "ブローカー","key" : "broaker"},
						{"header" : "BID","key" : "bid"},
						{"header" : "ASK","key" : "ask"},
						{"header" : "スプレッド","key" : "spread", "template" : "{{spread}}pips"}
					],
					sortBy : _sortBy,
					query : _query,
					template: _template,
					paginating: _paginating,
					size: _size
				});
			}
			if (searchBoxFocused) {
				var searchBoxValue = $('.ui-table-search').attr('value');
				$('.ui-table-search').val('');
				$('.ui-table-search').val(searchBoxValue);
				$('.ui-table-search').focus();
			}
			$(".symbol_header").text(symbol);
		};

		ws.onclose = function(event) {
			// console.log("close " + ws.url);
		}
		return ws;
	}
	;

	$(".symbols").on("click", function(e) {
		if ($(this).hasClass("title")) {
			return;
		}
		// activeの剥奪と付替え
		$(this).parent().children(".active").removeClass("active");
		ws.close();

		// WebSocketのオープン
		$(this).addClass("active");
		symbol = $(this).text();
		ws = openWs();
	});

});
