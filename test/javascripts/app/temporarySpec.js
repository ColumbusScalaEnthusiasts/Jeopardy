describe ("A Demonstrator", function () {
	var subject;
	
	beforeEach (function () {
		subject = Temporary.Demonstrator;
	});
	
	describe ("when directed to testMe", function () {
		var result;
		
		beforeEach (function () {
			spyOn (Infrastructure, "somethingMockable");
			result = subject.testMe ("first", "second");
		});
		
		it ("makes a call to Infrastructure", function () {
			expect (Infrastructure.somethingMockable).toHaveBeenCalledWith ("first", "second");
		});
		
		it ("returns the sum of its parameters", function () {
			expect (result).toBe ("firstsecond");
		});
	});
});