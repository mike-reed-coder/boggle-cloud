(ns boggle.web
  (:require [boggle.boggle :as boggle]
            [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.middleware.json :as ring-json]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.basic-authentication :as basic]
            [ring.util.response :as ring-response]
            [environ.core :refer [env]]))

(defn- authenticated? [user pass]
  ;; TODO: heroku config:add REPL_USER=[...] REPL_PASSWORD=[...]
  (= [user pass] [(env :repl-user false) (env :repl-password false)]))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn wrap-default-content-type
  "Forces the content type to be 'application/json'. Assuming all JSON for this project."
  [handler]
  (fn [req]
    (handler (assoc req :content-type "application/json"))))

(defroutes app-routes
  (POST "/boggle" [:as {body :body}] (boggle/solve-board body))
  (GET "/" [] (slurp (io/resource "index.html")))
  (GET "/js/:js-file" [js-file] (slurp (io/resource (str "js/" js-file))))
  (GET "/css/:css-file" [css-file] {:status 200 :headers {"content-type" "text/css"} :body (slurp (io/resource (str "css/" css-file)))})
  (ANY "*" [] (route/not-found (slurp (io/resource "404.html")))))

(def app (-> #'app-routes
           ((if (env :production) wrap-error-page trace/wrap-stacktrace))
           (site {:session {:store (cookie/cookie-store {:key (env :session-secret)})}})
           (ring-json/wrap-json-body {:keywords? true})
           (ring-json/wrap-json-response {:keywords? true})
           (wrap-default-content-type)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty #'app {:port port :join? false})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
