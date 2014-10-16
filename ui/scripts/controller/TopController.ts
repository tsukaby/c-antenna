///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Sample {
  "use strict";

  export interface IScope extends ng.IScope {
    sites: Array<any>;
  }

  export class TopController {

    constructor(public $scope:IScope, private $http:ng.IHttpService) {
      $http.get("/api/sites").success((data:Array<Model.Site>) => {
        $scope.sites = data;
      });
    }

  }
}
