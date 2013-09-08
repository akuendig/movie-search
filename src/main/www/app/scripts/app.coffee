'use strict';

angular.module('ms.services', ['angular-underscore', 'restangular'])

angular.module('ms.app', ['ms.services'])
  .config ['$routeProvider', 'RestangularProvider', ($routeProvider, RestangularProvider) ->
    RestangularProvider.setBaseUrl('http://localhost:9000/api')
    $routeProvider
    .when '/',
      templateUrl: 'views/main.html'
      controller: 'MovieCtrl'
    .otherwise
      redirectTo: '/'
  ]

