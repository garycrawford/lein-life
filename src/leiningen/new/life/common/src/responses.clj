(ns {{ns-name}}.responses)

(defn model-view-response
  [body status-code]
  {:status status-code
   :headers {"Content-Type" "text/html"}
   :body body})

(defn model-view-404
  [body]
  (model-view-response body 404))

(defn model-view-200
  [body]
  (model-view-response body 200))

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
