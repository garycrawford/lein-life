(ns {{ns-name}}.logging-config
  (:require [taoensso.timbre :refer [str-println merge-config!]])
  (:import [java.io OutputStreamWriter]))

(def info-timbre-config
  "A basic timbre configuration for use with info level logging."
  {:appenders
   {:standard-out
    {:doc "Prints to *out*/*err*."
     :min-level :info :enabled? true :async? false :rate-limit nil
     :fn (fn [{:keys [error? output]}]
           (binding [*out* (if error?
                             (OutputStreamWriter. System/err)
                             (OutputStreamWriter. System/out))]
             (str-println output)))}}})

(merge-config! info-timbre-config)
