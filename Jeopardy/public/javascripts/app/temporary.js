var Infrastructure = {
	somethingMockable: function (a, b) {
		window.alert ("Should never see this");
	}
}

var Temporary = Temporary || {};

Temporary.Demonstrator = (function () {
	var self = {};
	
	self.testMe = function (a, b) {
		Infrastructure.somethingMockable (a, b);
		return a + b;
	}
	
	return self;
}) ();