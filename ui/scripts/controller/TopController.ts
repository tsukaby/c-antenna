///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />

module Sample {
  "use strict";

  export interface IScope extends ng.IScope {
    sites: Array<any>;

    totalItems: number;
    currentPage: number;

    maxSize:number;
    bigTotalItems:number;
    bigCurrentPage:number;

    pageChanged: Function;

    // 検索条件 ページング条件
    condition:Model.SimpleSearchCondition;

  }

  export class TopController {

    constructor(public $scope:IScope, private $http:ng.IHttpService) {
      $scope.condition = new Model.SimpleSearchCondition();
      $scope.condition.page = 1;
      $scope.condition.count = 9;

      $http.get("/api/sites?" + $.param($scope.condition)).success((data:Model.Page<Model.Site>) => {
        $scope.sites = data.items;
        $scope.totalItems = data.total;
      });

      $scope.totalItems = 0;
      $scope.currentPage = 1;

      $scope.pageChanged = function () {
        console.log('Page changed to: ' + $scope.currentPage);
      };

      $scope.maxSize = 5;
      $scope.bigTotalItems = 175;
      $scope.bigCurrentPage = 1;
    }

  }
}
