import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import org.w3c.fetch.Response
import org.w3c.workers.Cache
import org.w3c.workers.ExtendableEvent
import org.w3c.workers.FetchEvent
import org.w3c.workers.ServiceWorkerGlobalScope


external val self: ServiceWorkerGlobalScope

const val version = "8"

fun main() {

    lateinit var cache: Cache

    self.oninstall = {
        console.log("Service Worker $version installing...")
        it as ExtendableEvent
        it.waitUntil(MainScope().promise {
            cache = self.caches.open("cache").await()
            self.fetch("resources.txt").await().text().await().lines().forEach {
                try {
                    cache.put(it, self.fetch(it).await())
                } catch (ex: Throwable) {
                    println("Could not install $it in cache")
                }
            }
        })
    }

    self.onactivate = {
        console.log("Service Worker $version active!")
        it as ExtendableEvent
        it.waitUntil(MainScope().promise {
            cache = self.caches.open("cache").await()
        })
    }

    self.addEventListener("fetch", {
        it as FetchEvent
//        console.log("$version: ${it.request.url}")
        it.respondWith(MainScope().promise {
            try {
                val response = self.fetch(it.request).await()
                cache.put(it.request, response.clone()).await()
                response
            } catch (ex: Throwable) {
                val res = cache.match(it.request).await() ?: throw ex
                res as Response
            }
        })
    })
}
