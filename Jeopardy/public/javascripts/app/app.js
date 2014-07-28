App = Ember.Application.create();

App.Router.map(function() {
	this.route ('vestibule');
	this.resource ('candidates', {path: '/vestibule/candidates'});
});
