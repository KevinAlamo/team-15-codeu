/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Adds navigational links to the page, depending on whether the user is
 * already logged in.
 */
function loadNavigation() {              // eslint-disable-line no-unused-vars
  const navigationElement = document.getElementById('navigation');
  if (!navigationElement) {
    console.warn('Navigation element not found!');
    return;
  }

  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn) {
          navigationElement.appendChild(
            createListItem(createLink('/index.html', 'Home')));

          navigationElement.appendChild(createListItem(createLink(
              '/user-page.html?user=' + loginStatus.username, 'Your Page')));

          navigationElement.appendChild(
            createListItem(createLink('/aboutus.html', 'About Our Team')));

          navigationElement.appendChild(
            createListItem(createLink('/chart.html', 'Chart')));

          navigationElement.appendChild(
            createListItem(createLink('/map.html', 'Map')));

          navigationElement.appendChild(
            createListItem(createLink('/community.html', 'Site Community')));

          navigationElement.appendChild(
            createListItem(createLink('/activityfeed.html', 'Activity Feed')));

          navigationElement.appendChild(
            createListItem(createLink('/feed.html', 'Message Feed')));

          navigationElement.appendChild(
            createListItem(createLink('/stats.html', 'Site Stats')));

          navigationElement.appendChild(
              createListItem(createLink('/logout', 'Logout')));

        } else {
          navigationElement.appendChild(
              createListItem(createLink('/login', 'Login')));
        }
      });
}

/**
 * Creates an li element.
 * @param {Element} childElement
 * @return {Element} li element
 */
function createListItem(childElement) {
  const listItemElement = document.createElement('li');
  listItemElement.appendChild(childElement);
  return listItemElement;
}

/**
 * Creates an anchor element.
 * @param {string} url
 * @param {string} text
 * @return {Element} Anchor element
 */
function createLink(url, text) {
  const linkElement = document.createElement('a');
  linkElement.appendChild(document.createTextNode(text));
  linkElement.href = url;
  return linkElement;
}