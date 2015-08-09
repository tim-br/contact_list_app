(ns contact-app-json.routes.home
  (:require [contact-app-json.layout :as layout]
            [contact-app-json.db.core :as db]
            [clojure.data.json :as json]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render "home.html"))

(defn yolo []
  (db/get-contacts))

;; (defn foo [{:keys [params]}]
;;   (println (json/write-str params)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (POST "/contact/new" request (db/create-contact! request))
  (GET "/docs" [] (ok (-> "docs/docs.md" io/resource slurp))))
