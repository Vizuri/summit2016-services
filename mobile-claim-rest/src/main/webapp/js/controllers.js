(function() {
	'use strict';

	angular.module('bpmsFlowApp.controllers', []);

	angular.module('bpmsFlowApp.controllers').controller('MainController', mainController);

	function mainController($log, $http) {
		$log.info('Inside MainController');
		var vm = this;

		vm.processId = '193'; // Default processId

		vm.loadComments = loadComments;
		vm.loadImage = loadImage;

		function loadComments() {
			$http({
				method : 'GET',
				withCredentials : true,
				url : 'http://localhost:8080/business-central/rest/runtime/com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT/process/instance/' + vm.processId + '/variable/claimComments'
			}).then(function(response) {
				$log.info(response.data);
			}, function(error) {
				$log.error(error);
			});
		}

		function loadImage() {
			$http({
				method : 'GET',
				withCredentials : true,
				url : 'http://localhost:8080/business-central/rest/runtime/com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT/process/mobile-claims-bpm.mobile-claim-process/image/' + vm.processId
			}).then(function(response) {
				document.getElementById('image').innerHTML = response.data;
			}, function(error) {
				$log.error(error);
			});
		}

		loadImage();
		loadComments();

	}

})();