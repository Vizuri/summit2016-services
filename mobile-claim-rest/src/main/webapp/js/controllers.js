(function() {
	'use strict';

	angular.module('bpmsFlowApp.controllers', []);

	angular.module('bpmsFlowApp.controllers').controller('MainController', mainController);

	function mainController($log, $http) {
		$log.info('Inside MainController');
		var vm = this;

		vm.message = 'Up and running!';

		$http({
			method : 'GET',
			withCredentials : true,
			url : 'http://localhost:8080/business-central/rest/runtime/com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT/process/mobile-claims-bpm.mobile-claim-process/image/193'
		}).then(function(response) {
			$log.info(response);
		}, function(error) {
			$log.error(error);
		});

	}

})();