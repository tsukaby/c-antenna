///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Sample {
  "use strict";

  export interface IScope extends ng.IScope {
    sites: Array<any>;
  }

  export class TopController {

    constructor(public $scope:IScope, private $http:ng.IHttpService) {
      var page:number = 1;
      var count:number = 9;
      $http.get("/api/sites?page=" + page + "&count=" + count).success((data:Array<Model.Site>) => {
        $scope.sites = data;
      });
    }

  }
}
