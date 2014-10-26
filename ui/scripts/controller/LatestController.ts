///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Sample {
  "use strict";

  export interface IScope extends ng.IScope {
    tableParams: any;
  }

  export class LatestController {

    constructor(public $scope:IScope, private $http:ng.IHttpService, private ngTableParams:any) {

      $http.get("/api/lately_articles").success((data:Array<Model.Article>) => {
        $scope.tableParams = new ngTableParams({
          page: 1,            // show first page
          count: 10           // count per page
        }, {
          total: data.length, // length of data
          getData: function ($defer:any, params:any) {
            $defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
          }
        });
      });
    }

  }
}
