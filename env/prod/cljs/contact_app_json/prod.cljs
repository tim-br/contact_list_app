(ns contact-app-json.app
  (:require [contact-app-json.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
