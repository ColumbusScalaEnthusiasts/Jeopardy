describe ("$.toJSON, when called", function () {
	var result = null;
	
	beforeEach (function () {
		spyOn (JSON, "stringify").andReturn ("output");
		result = $.toJSON ("input");
	});
	
	it ("delegates to JSON.stringify", function () {
		expect (JSON.stringify).toHaveBeenCalledWith ("input");
	});
	
	it ("returns the result from JSON.stringify", function () {
		expect (result).toBe ("output");
	})
});

describe ("$.evalJSON, when called", function () {
	var result = null;
	
	beforeEach (function () {
		spyOn (JSON, "parse").andReturn ("output");
		result = $.evalJSON ("input");
	});
	
	it ("delegates to JSON.parse", function () {
		expect (JSON.parse).toHaveBeenCalledWith ("input");
	});
	
	it ("returns the result from JSON.parse", function () {
		expect (result).toBe ("output");
	})
});
