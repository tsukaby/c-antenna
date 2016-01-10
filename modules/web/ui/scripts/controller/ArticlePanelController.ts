///<reference path='../../typings/angularjs/angular.d.ts' />
///<reference path='../../typings/angular-ui/angular-ui-router.d.ts' />

///<reference path='../Model.ts' />
///<reference path='../Service.ts' />

module ArticlePanelControllerModule {
  "use strict";

  export interface IScope extends ng.IScope {
    // 検索結果
    articles:Array<Model.Article>;
    totalItems:number;

    currentPage: number;
    maxId: number;

    // 検索条件 ページング条件
    condition:Model.SimpleSearchCondition;

    clickLogService:Service.ClickLogService;

    // ページ変更時の処理 再検索
    pageChanged: Function;

    // 検索処理
    loadData: () => void;
  }

  export class ArticlePanelController {

    private categoryMap:any = {
      funny: 1,
      foodie: 2,
      sport: 3,
      entertainment:4,
      government:5,
      technology: 6,
      anime: 7,
      r18:8
    };

    constructor(public $scope:IScope,
                private $http:ng.IHttpService,
                private $state:ng.ui.IStateService,
                private $stateParams:ng.ui.IStateParamsService,
                private clickLogService:Service.ClickLogService) {

      $scope.clickLogService = clickLogService;

      $scope.articles = [];
      $scope.totalItems = 0;

      $scope.condition = new Model.SimpleSearchCondition();
      var categoryId:any = this.categoryMap[$stateParams["type"]];
      $scope.condition.categoryId = categoryId ? categoryId : null;
      $scope.condition.page = $stateParams["page"] ? $stateParams["page"] : 1;
      $scope.condition.count = 20;
      $scope.condition.hasEyeCatch = true;
      if (!!$stateParams["maxId"]) {
        $scope.maxId = $stateParams["maxId"];
      }
      if ($scope.condition.page !== 1 && !!$stateParams["maxId"]) {
        $scope.condition.maxId = $stateParams["maxId"];
      }

      // State check
      if ($scope.condition.page !== 1 && !$scope.condition.maxId) {
        $state.go("articles", {}, {inherit: false});
        return;
      }

      $scope.loadData = () => {
        $http.get("/api/articles?" + $.param($scope.condition)).success((data:Model.Page<Model.Article>) => {
          $scope.currentPage = $stateParams["page"] ? $stateParams["page"] : 1;
          this.$scope.articles = data.items;
          this.$scope.totalItems = data.total;

          if (!this.$scope.maxId) {
            this.$scope.maxId = data.items[0].id;
          }

        });
      };

      $scope.pageChanged = function () {
        var params:any = {
          page: $scope.currentPage,
          type: $stateParams["type"]
        };
        if (params.page !== 1) {
          params.maxId = $scope.maxId;
        }

        if (!!$scope.condition.categoryId) {
          $state.go("categories", params, {inherit: true});
        } else {
          $state.go("articles", params, {inherit: true});
        }

      };

      // 初期データロード
      $scope.loadData();

    }
  }
}
