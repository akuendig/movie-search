'use strict'

angular.module('ms.app')
  .controller 'MovieCtrl', ['$scope', 'Movie', ($scope, Movie) ->
     result = Movie.query().then (data) -> _.map data, (movie) ->
      id: movie.id
      dirName: movie.dirname
      extId: movie.extInfo.id
      title: movie.extInfo.title

     $scope.movies = result

  ]