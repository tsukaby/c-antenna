/**
 * モデルのモジュール。
 */
module Model {
  "use strict";

  export class Sample {
    test:string;

    /**
     * @constructor
     * @param data JSONObjectまたはJSON文字列
     */
    constructor(data:any) {
      if (angular.isString(data)) {
        data = angular.fromJson(data);
      }
      this.test = data.test;
    }
  }

  export class Site {
    id:number;
    name:string;
    url:string;
    thumbnail:string;

  }
}
