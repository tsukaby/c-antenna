///<reference path='../../typings/angularjs/angular.d.ts' />

///<reference path='../Model.ts' />
///<reference path='../Service.ts' />

module TopControllerModule {
  "use strict";

  export interface IScope extends ng.IScope {
    sites: Array<any>;

    totalItems: number;
    currentPage: number;

    maxSize:number;
    bigTotalItems:number;
    bigCurrentPage:number;

    // ページ変更時の処理 再検索
    pageChanged: Function;

    // 検索条件 ページング条件
    condition:Model.SimpleSearchCondition;

    // 検索処理
    loadData: () => void;

    clickLogService:Service.ClickLogService;
  }

  export class TopController {

    constructor(public $scope:IScope,
                private $http:ng.IHttpService,
                private clickLogService:Service.ClickLogService) {

      $scope.clickLogService = clickLogService;

      $scope.condition = new Model.SimpleSearchCondition();
      $scope.condition.page = 1;
      $scope.condition.count = 9;
      $scope.condition.sort = new Model.Sort("HATEBU_COUNT", Model.SortOrder.Desc);

      $scope.totalItems = 0;
      $scope.currentPage = 1;

      $scope.pageChanged = function () {
        $scope.condition.page = $scope.currentPage;
        this.loadData();
      };

      $scope.maxSize = 10;

      $scope.loadData = () => {
        this.$http.get("/api/sites?" + $.param(this.$scope.condition)).success((data:Model.Page<Model.Site>) => {
          this.$scope.sites = data.items;
          this.$scope.totalItems = data.total;
        });
      };

      // 初期データロード
      $scope.loadData();

    }


  }
}
