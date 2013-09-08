'use strict';

angular.module('ms.services')
  .factory 'Movie', ['Restangular', (Restangular) ->
    # Service logic
    # ...

    meaningOfLife = 42

    # Public API here
    {
      query: () ->
        console.log('Query was called')
        Restangular.all('movies').getList({skip: 0, take: 20})
    }
  ]
