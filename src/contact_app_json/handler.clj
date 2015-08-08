(ns contact-app-json.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [contact-app-json.layout :refer [error-page]]
            [contact-app-json.routes.home :refer [home-routes]]
            [contact-app-json.routes.services :refer [service-routes]]
            [contact-app-json.middleware :as middleware]
            [contact-app-json.db.core :as db]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.3rd-party.rotor :as rotor]
            [selmer.parser :as parser]
            [environ.core :refer [env]]))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  []

  (timbre/merge-config!
    {:level     (if (env :dev) :trace :info)
     :appenders {:rotor (rotor/rotor-appender
                          {:path "contact_app_json.log"
                           :max-size (* 512 1024)
                           :backlog 10})}})

  (if (env :dev) (parser/cache-off!))
  (db/connect!)
  (timbre/info (str
                 "\n-=[contact_app_json started successfully"
                 (when (env :dev) " using the development profile")
                 "]=-")))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "contact_app_json is shutting down...")
  (db/disconnect!)
  (timbre/info "shutdown complete!"))

(def app-routes
  (routes
    (var service-routes)
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (error-page {:code 404
                   :title "page not found"}))))

#_(def app
  (-> (routes
        home-routes ;;no CSRF protection
        #_base-routes)
      middleware/wrap-base))

(def app (middleware/wrap-base #'app-routes))
