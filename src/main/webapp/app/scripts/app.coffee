angular.module('movieSearchApp', ['movieSearchServices'])
  .config ['$routeProvider', 'RestangularProvider', ($routeProvider, RestangularProvider) ->
    RestangularProvider.setBaseUrl('http://localhost:9000/api')
    $routeProvider
    .when '/',
      templateUrl: 'views/main.html'
      controller: 'MainCtrl'
    .otherwise
      redirectTo: '/'
  ]
