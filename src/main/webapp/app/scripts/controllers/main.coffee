angular.module('movieSearchApp')
  .controller 'MainCtrl', ['$scope', 'Movie', ($scope, Movie) ->
    $scope.awesomeThings = Movie.query()
  ]
