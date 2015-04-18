(ns {{ns-name}}.responses)

(defn model-view-ok
  [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn json-ok
  [body]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    body})

(defn wrap-view-response
  [handler]
  (fn [request]
    (let [response (handler request)
          view-fn  (get-in response [:body :view :fn])
          model    (get-in response [:body :model])]
      (if (and view-fn model)
        (assoc response :body (view-fn model))
        response))))
