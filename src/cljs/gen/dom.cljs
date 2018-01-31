(ns gen.dom)


(defn create-element [type]
  (.createElement js/document type))


(defn append [parent el]
  (.appendChild parent el))


(defn prepend [parent el]
  (.insertBefore parent el (.-firstChild parent)))


(defn $ [sel]
  (.querySelector body sel))


(defn $$ [sel]
  (.querySelectorAll body sel))


(def body (.-body js/document))