'use strict';

angular.module('ms.services')
  .factory 'Movie', ['Restangular', (Restangular) ->
    # Service logic
    # ...

    # Public API here
    {
      query: () ->
        Restangular.all('movies').getList({skip: 0, take: 20})
    }
  ]
