angular.module('movieSearchServices', ['ngResource']).
  factory 'Movie', ['$resource', ($resource) ->
    $resource 'api/search', {}, {
      query:
        method:'GET'
        params:
          skip: 10
          take: 20
        isArray:true
    }
  ]
