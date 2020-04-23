package py.com.bancop.app.utils.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Created by Diana Melgarejo on 4/16/20.
 *
 * In testing our Rx code with J-unit, we need to account for the fact that
 * the Android main thread is not available in the test environment.
 * To do so, we create an immediate scheduler that our test will use
 * to avoid the need for accessing the Android main thread.
 *
 * @trampoline: the trampoline method causes the Rx Task to execute on the current thread.
 */

class ImmediateSchedulerProvider : BaseSchedulerProvider {
    override fun computation(): Scheduler = Schedulers.trampoline()

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()

}