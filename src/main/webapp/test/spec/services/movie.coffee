'use strict'

describe 'Service: movie', () ->

  # load the service's module
  beforeEach module 'webappApp'

  # instantiate service
  movie = {}
  beforeEach inject (_movie_) ->
    movie = _movie_

  it 'should do something', () ->
    expect(!!movie).toBe true
