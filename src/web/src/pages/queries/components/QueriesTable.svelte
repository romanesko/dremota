{#await pQueries then queries}

<div class="table-responsive">
  <table class="table table-vcenter card-table">
    <thead>
    <tr>
      <th class="text-center">создано</th>
      <th class="text-center">user id</th>
      <th>контекст</th>
      <th>префикс</th>
      <th>запрос</th>
      <th>ответ</th>
      <th class="text-center">обновлено</th>
    </tr>
    </thead>
    <tbody>
    {#each queries as query}
      <tr>
        <td class="text-secondary align-top text-center">{formatDate(query.createdAt)}</td>
        <td class="text-secondary align-top text-center"><a href="/#/users/{query.chatId}">{query.chatId}</a></td>
        <td class="text-secondary align-top">{query.context}</td>
        <td class="text-secondary align-top">{query.prefix}</td>
        <td class="text-secondary align-top">{query.request}</td>
        <td class="text-secondary align-top">
          <div class="collapsed" aria-hidden="true" onclick={removeCollapse}>{@html query.response?.replaceAll('\n', '<br/>')}</div>

        </td>
        <td class="text-secondary align-top text-center">{formatDate(query.updatedAt)}</td>

      </tr>
    {/each}

    </tbody>
  </table>
</div>
  {:catch error}
  <div class="alert alert-danger" role="alert">
    {error}
  </div>
{/await}

<script lang="ts">


    import api from "@/lib/api";
    import {formatDate} from "@/lib/common";

    let pQueries = api.getQueries()
    function removeCollapse(e: any) {
      (e.target as HTMLElement).classList.remove('collapsed')
    }
</script>

<style>
  .collapsed {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 5;
    overflow: hidden;
  }
</style>