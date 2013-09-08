'use strict'

angular.module('ms.app')
  .controller 'MovieCtrl', ['$scope', 'Movie', ($scope, Movie) ->
     Movie.query {}, (data) ->
       $scope.movies = data.map (movie) ->
        id: movie.id
        dirName: movie.dirname
        extId: movie.extInfo.id
        title: movie.extInfo.title

  ]