(function() {
	'use strict';

	angular.module('bpmsFlowApp.controllers', []);

	angular.module('bpmsFlowApp.controllers').controller('MainController', mainController);

	function mainController($log, $http, $interval) {
		$log.info('Inside MainController');
		var vm = this;

		vm.processId = '225'; // Default processId
		vm.comments = [];
		vm.photos = [];

		vm.load = load;

		function load() {
			loadImage();
			loadComments();
			loadVars();
		}

		function loadVars() {
			$http({
				method : 'GET',
				withCredentials : true,
				url : '../business-central/rest/runtime/com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT/withvars/process/instance/' + vm.processId
			}).then(function(response) {
				var xml = response.data;
				var x2js = new X2JS();
				var document = x2js.xml2js(xml);
				var rx = /^photo[0-9]?$/m;
				var vars = document['process-instance-with-vars-response'].variables.entry;
				for (var i = 0; i < vars.length; i++) {
					if (rx.test(vars[i].key)) {
						vm.photos.push('http://104.197.211.18/business-central/' + vars[i].value.split('####')[3]);
					}
				}
			}, function(error) {
				$log.error(error);
			});
		}

		function loadComments() {
			$http({
				method : 'GET',
				withCredentials : true,
				url : '../business-central/rest/runtime/com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT/process/instance/' + vm.processId + '/variable/claimComments'
			}).then(function(response) {
				var xml = response.data;
				if (xml) {
					var x2js = new X2JS();
					var document = x2js.xml2js(xml);
					vm.comments = document['list-type'].value;
				}
			}, function(error) {
				$log.error(error);
			});
		}

		function loadImage() {
			$http({
				method : 'GET',
				withCredentials : true,
				url : '../business-central/rest/runtime/com.redhat.vizuri.insurance:mobile-claims-bpm:1.0-SNAPSHOT/process/mobile-claims-bpm.mobile-claim-process/image/' + vm.processId
			}).then(function(response) {
				document.getElementById('image').innerHTML = response.data;
			}, function(error) {
				$log.error(error);
			});
		}

		load();

		$interval(function() {
			$log.info('Updating...');
			load();
		}, 10000);

	}

})();