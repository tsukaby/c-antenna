/**
 * モデルのモジュール。
 */
module Model {
  "use strict";

  export class Site {
    id:number;
    name:string;
    url:string;
    thumbnail:string;
  }

  export class Article {
    url:string;
    title:string;
    thumbnail:string;
    tags:string;
    siteName:string;
    createdAt:Date;
  }
}
