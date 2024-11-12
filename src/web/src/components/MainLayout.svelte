<div class="page">
  <header class="navbar navbar-expand-sm navbar-light d-print-none">
    <div class="container-xl">
      <h1 class="navbar-brand navbar-brand-autodark d-none-navbar-horizontal pe-0 pe-md-3">
        <a href="/" style="text-decoration: none;">

          <img alt="DREMOTA" src={logo} style="height: 2.2rem; width: auto"/>
          DREMOTA

        </a>
      </h1>


      <div class="row flex-fill align-items-center">
        <div class="col">
          <ul class="navbar-nav">
            <li class="nav-item">
              <a class="nav-link" href="/">
                <span class="nav-link-title"> Пользователи </span>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/#/queries">
                <span class="nav-link-title"> Запросы </span>
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/#/settings">
                <span class="nav-link-title"> Настройки </span>
              </a>
            </li>
          </ul>
        </div>


      </div>

      {#if $currentUser}
        <div class="navbar-nav flex-row order-md-last">
          <div class="nav-item">
            <div aria-hidden="true" class="nav-link d-flex lh-1 text-reset p-0">
              <span class="avatar avatar-sm" style="background-image: url({getUserPhoto($currentUser)})"></span>
              <div class="d-none d-xl-block ps-2">
                <div>{$currentUser.firstName} {$currentUser.lastName}</div>
                {#if $currentUser.username}
                  <div class="mt-1 small text-secondary">@{$currentUser.username}</div>
                {/if}
              </div>
            </div>
          </div>
        </div>
      {/if}
    </div>
  </header>
  <div class="page-wrapper">
    <div class="page-body">
      <div class="container-xl">
        <div class="row row-deck row-cards">


          <Router
              routes={{
        '/': UsersPage,
        '/queries': wrap({ asyncComponent: () => import("@/pages/queries/QueriesPage.svelte") }),
        '/users/:chatId': wrap({ asyncComponent: () => import("@/pages/user/UserPage.svelte") }),
        '/settings':  wrap({ asyncComponent: () => import("@/pages/settings/SettingsPage.svelte") }),
        '/settings/:tab': wrap({ asyncComponent: () => import("@/pages/settings/SettingsPage.svelte") })
      }}/>


        </div>
      </div>
    </div>
  </div>
</div>

<div
    id="toast-container" class="toast-container position-fixed top-0 end-0 p-3"

>

</div>

<script>
    import logo from '@/assets/dremota.svg';
    import UsersPage from "@/pages/users/UsersPage.svelte";
    import Router from "svelte-spa-router";

    import {currentUser} from "@/store/user";
    import {getUserPhoto} from "@/lib/common";

    import {wrap} from 'svelte-spa-router/wrap'
</script>