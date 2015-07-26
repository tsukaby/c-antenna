/**
 * モデルのモジュール。
 */
module Model {
  "use strict";

  /**
   * Webサイト
   */
  export class Site {
    id:number;
    name:string;
    url:string;
    thumbnail:string;
    clickCount:number;
    hatebuCount:number;
    tweetCount:number;
  }

  /**
   * 記事
   */
  export class Article {
    id:number;
    siteId:number;
    url:string;
    eyeCatchUrl:string;
    title:string;
    thumbnail:string;
    tags:string;
    siteName:string;
    clickCount:number;
    hatebuCount:number;
    tweetCount:number;
    publishedAt:Date;
  }

  /**
   * 簡単な検索条件。ページング用。
   */
  export class SimpleSearchCondition {
    page:number;
    count:number;

    hasEyeCatch:boolean;

    // 期間範囲指定
    startDateTime:string;
    endDateTime:string;

    sort:Sort;
  }

  export class Sort {
    key:string;
    order:SortOrder;

    constructor(key:string, order:SortOrder) {
      this.key = key;
      this.order = order;
    }
  }

  export enum SortOrder {
    Unknown,
    Asc,
    Desc
  }

  /**
   * ページングによる検索の結果
   */
  export class Page<T> {
    items:Array<T>; //取得したオブジェクト
    total:number; //ページングしない場合の全体件数
  }

  /**
   * クリック情報を送信する為のクラス
   */
  export class ClickLog {
    siteId:number;
    articleId:number;
  }
}
