'use strict';

angular.module('docs').controller('UserGroup', function(Restangular, $scope, $state, $translate) {
  // Load users
  Restangular.one('user/list').get({
    sort_column: 1,
    asc: true
  }).then(function(data) {
    $scope.users = data.users;
  });

  // Load groups
  Restangular.one('group').get({
    sort_column: 1,
    asc: true
  }).then(function(data) {
    $scope.groups = data.groups;
  });

  // Open a user
  $scope.openUser = function(user) {
    $state.go('user.profile', { username: user.username });
  };

  // Open a group
  $scope.openGroup = function(group) {
    $state.go('group.profile', { name: group.name });
  };

  // Initialize registration form data
  $scope.register = {
    username: '',
    email: '',
    password: '',
    message: ''
  };

  $scope.alerts = [];

  // Submit registration request
  $scope.submitRegisterRequest = function() {
    Restangular.one('user_request/submit').post('', $.param($scope.register), {
      headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    })
    .then(function() {
      $scope.alerts.push({
        type: 'success',
        msg: $translate.instant('usergroup.register.success')
      });
      // Reset form
      $scope.register = {
        username: '',
        email: '',
        password: '',
        message: ''
      };
      $scope.registerForm.$setPristine();
    })
    .catch(function(e) {
      $scope.alerts.push({
        type: 'danger',
        msg: e.data.message || $translate.instant('usergroup.register.error')
      });
    });
  };

  // Close alert message
  $scope.closeAlert = function(index) {
    $scope.alerts.splice(index, 1);
  };

  // Load pending requests for admin
  // Load pending requests and history for admin
  if ($scope.userInfo && $scope.userInfo.base_functions.indexOf('ADMIN') !== -1) {
      // Load pending requests
      Restangular.one('user_request/pending').get().then(function(data) {
          $scope.pendingRequests = data.requests;
      });
      
      // Load request history
      Restangular.one('user_request/history').get().then(function(data) {
          $scope.requestHistory = data.requests;
      });
  
      // Refresh function to update both lists
      $scope.refreshLists = function() {
          Restangular.one('user_request/pending').get().then(function(data) {
              $scope.pendingRequests = data.requests;
          });
          Restangular.one('user_request/history').get().then(function(data) {
              $scope.requestHistory = data.requests;
          });
      };
  }

  // View request details
  $scope.viewRequest = function(request) {
    $scope.selectedRequest = request;
    $state.go('usergroup.request', { name: request.name });
  };

  // Handle request actions
  // Update handleRequest to use the refresh function
  $scope.handleRequest = function(request, action) {
      Restangular.one('user_request/' + action + '/' + request.name).post()
          .then(function() {
              $scope.refreshLists();
              $scope.alerts.push({
                  type: 'success',
                  msg: $translate.instant('usergroup.request.' + action + '_success')
              });
          })
          .catch(function(e) {
              $scope.alerts.push({
                  type: 'danger',
                  msg: e.data.message || $translate.instant('usergroup.request.' + action + '_error')
              });
          });
  };
});