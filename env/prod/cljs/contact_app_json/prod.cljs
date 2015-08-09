(ns contact-app-json.app
  (:require [contact-app-json.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(System/getenv "postgres://ngwcurccalfnhm:DRZEQdinkQlGE243yFFXlRuAHo@ec2-54-83-51-0.compute-1.amazonaws.com:5432/db7e53sn8ggeep")


(core/init!)
