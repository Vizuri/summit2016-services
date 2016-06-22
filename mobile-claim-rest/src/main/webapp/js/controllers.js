(function() {
	'use strict';

	angular.module('bpmsFlowApp.controllers', []);

	angular.module('bpmsFlowApp.controllers').controller('MainController', mainController);

	function mainController($log) {
		$log.info('Inside MainController');
		var vm = this;

		vm.message = 'Up and running!';

	}

})();