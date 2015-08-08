(ns contact-app-json.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand {:href "#/"} "myapp"]]
    [:div.navbar-collapse.collapse
     [:ul.nav.navbar-nav
      [:li {:class (when (= :api-demo (session/get :page)) "active")}
        [:a {:href "#/api-demo"} "Api Demo"]]
      [:li {:class (when (= :home (session/get :page)) "active")}
       [:a {:href "#/"} "Home"]]
      [:li {:class (when (= :about (session/get :page)) "active")}
       [:a {:href "#/about"} "About"]]]]]])

(def app-state
  (atom
   {:contacts
    [{:name "Ben" :email "benb@mit.edu"}
     {:name "Alyssa" :email "aphacker@mit.edu" :phone "39820"}]}))


(def my-json (atom {:contacts []}))

(defn update-contacts! [f & args]
  (apply swap! app-state update-in [:contacts] f args))

(defn add-contact! [c]
  (update-contacts! conj c))

(defn remove-contact! [c]
  (update-contacts! (fn [cs]
                      (vec (remove #(= % c) cs)))
                    c))

(defn display-name [{:keys [name email phone] :as contact}]
  (str name " " email " " phone ))

(defn contact [c]
  [:li
   [:span (display-name c)]])

(defn contact-list []
  [:div
   [:h1 "Contact list"]
   [:ul
    (for [c (:contacts @my-json)]
      [contact c])]])


(defn list-contacts []
  (GET "/api/my-contacts"
       {:headers {"Accept" "application/transit+json"}
        :handler (fn [response] (do
                                 (swap! my-json assoc :contacts response)))}))

(defn contacts-button []
  [:div
   [:br]
   [:input {:type "button" :value "Show Contacts!"
            :on-click #( do
                         (list-contacts))}]])


(defn add [params result]
  (GET "/api/plus"
       {:headers {"Accept" "application/transit+json"}
        :params @params
        :handler #(reset! result %)}))

(defn create-new-contact [params result]
  (POST "/api/contacts/new"
        {:headers {"Accept" "application/transit+json"}
         :params @params
         :handler #(do (reset! result %)
                       (list-contacts))}))

(defn int-value [v]
  (-> v .-target .-value int))

(defn str-value [v]
  (-> v .-target .-value str))


(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console
    (str "something bad happened: " status " " status-text)))

(defn button-component []
  [:div
   [:input {:type "button" :value "List all users!"
            :on-click #(( do
                          (js/alert "yolo")))}]])

(defn new-contact-form []
  (let [params (atom {})
        result (atom nil)]
    (fn []
      [:div
       [:form
        [:div.form-group
         [:label "Name"]
         [:input
          {:type :text
           :on-change #(swap! params assoc :name (str-value %))}]]
        [:div-form-group
         [:label "E-Mail"]
         [:input
          {:type :text
           :on-change #(swap! params assoc :email (str-value %))}]]

        [:div-form-group
         [:label "Phone Number"]
         [:input
          {:type :text
           :on-change #(swap! params assoc :phone (str-value %))}]]]
       [:button.btn.btn-primary {:on-click (fn [event] (do
                                                        (.preventDefault event)
                                                        (create-new-contact params result)
                                                        (js/console.log @params)
                                                        (js/console.log "result : " @result)))} "Add New Contact"]
])))


(defn about-page []
  [:div.container
   [:div.row
    [new-contact-form]
    [contacts-button]
    [contact-list]

    [:div #_[:h4 "" @my-json ""]]
    [:div #_[contact-list]]
    [:div.col-md-12
     "this is the story of contact_app_json... work in progress"]]])


(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to contact_app_json"]
    [:p "Time to start building your site!"]
    [:p [:a.btn.btn-primary.btn-lg {:href "http://luminusweb.net"} "Learn more »"]]]
   [:div.row
    [:div.col-md-12
     [:h2 "Welcome to ClojureScript"]]]
   (when-let [docs (session/get :docs)]
     [:div.row
      [:div.col-md-12
       [:div {:dangerouslySetInnerHTML
              {:__html (md->html docs)}}]]])])

(defn api-demo []
  (let [params (atom {})
        result (atom nil)]
    (fn []
      [:div
       [:form
        [:div.form-group
         [:label "x"]
         [:input
          {:type :text
           :on-change #(swap! params assoc :x (int-value %))}]]
        [:div.form-group
         [:label "y"]
         [:input
          {:type :text
           :on-change #(swap! params assoc :y (int-value %))}]]]
       [:button.btn.btn-primary {:on-click #(add params result)} "Add"]
       (when @result
         [js/alert "yolo"]
         [:p "result: " @result])])))

(def pages
  {:home #'home-page
   :about #'about-page
   :api-demo #'api-demo})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

(secretary/defroute "/api-demo" []
  (session/put! :page :api-demo))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          EventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(session/put! :docs %)}))

(defn mount-components []
  (reagent/render [#'navbar] (.getElementById js/document "navbar"))
  (reagent/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
