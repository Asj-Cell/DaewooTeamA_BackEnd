import { createRouter, createWebHistory } from 'vue-router';

// 뷰 컴포넌트 import
import Home from '@/components/Home.vue';
import HotelSearch from '@/components/HotelSearch.vue';
import HotelDetail from '@/components/HotelDetail.vue';
import PaymentPage from '@/components/PaymentPage.vue';
import Favorites from '@/components/Favorites.vue';
import Account from '@/components/Account.vue';
import Reservation from '@/components/Reservation.vue';
import PaymentAdd from '@/components/PaymentAdd.vue';
import Login from '@/components/Login.vue';
import SignUp from '@/components/SignUp.vue';
import FirstPaymentAdd from '@/components/FirstPaymentAdd.vue';
import Findpw from '@/components/Findpw.vue';
import Identification from '@/components/Identification.vue';
import Resetpw from '@/components/Resetpw.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL), // Vue 3 + Vite에서는 process.env 대신 import.meta.env 사용
    routes: [
        { path: '/', name: 'home', component: Home },
        { path: '/hotelsearch', name: 'hotelsearch', component: HotelSearch },
        { path: '/hoteldetail', name: 'hoteldetail', component: HotelDetail },
        { path: '/paymentpage', name: 'paymentpage', component: PaymentPage },
        { path: '/favorites', name: 'favorites', component: Favorites },
        { path: '/account', name: 'account', component: Account },
        { path: '/reservation', name: 'reservation', component: Reservation },
        { path: '/paymentadd', name: 'paymentadd', component: PaymentAdd },
        { path: '/login', name: 'login', component: Login },
        { path: '/signup', name: 'signup', component: SignUp },
        { path: '/firstpaymentadd', name: 'firstpaymentadd', component: FirstPaymentAdd },
        { path: '/findpw', name: 'findpw', component: Findpw },
        { path: '/identification', name: 'identification', component: Identification },
        { path: '/resetpw', name: 'resetpw', component: Resetpw },
    ],
});

export default router;
